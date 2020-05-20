'use strict';
const SERVER_URL = 'http://localhost:8000';

$(document).ready(function(){
    let StateViewModel = function () {
        let self = this;
        self.students = ko.observableArray();
        self.courses = ko.observableArray();
        self.grades = ko.observableArray();
        self.possibleGradeValue = ko.observableArray([
            {name: '2.0', value: 'NIEDOSTATECZNY'},
            {name: '3.0', value: 'DOSTATECZNY'},
            {name: '3.5', value: 'DOSTATECZNY_PLUS'},
            {name: '4.0', value: 'DOBRY'},
            {name: '4.5', value: 'DOBRY_PLUS'},
            {name: '5.0', value: 'BARDZO_DOBRY'}
        ]);
        self.searchOptions = ko.observableArray([
            {name: 'lt', value: 'Less than'},
            {name: 'eq', value: 'Equal'},
            {name: 'gt', value: 'Greater than'}
        ]);
        self.newStudent = {
            firstName: ko.observable(),
            lastName: ko.observable(),
            dateOfBirth: ko.observable()
        };
        self.studentSubscription = null;
        self.courseSubscription = null;
        self.newCourse = {
            name: ko.observable(),
            supervisor: ko.observable()
        };
        self.studentFilters = {
            first_name: ko.observable(),
            last_name: ko.observable(),
            birth_date: ko.observable(),
            order: ko.observable()
        };
        self.courseFilters = {
            name: ko.observable(),
            supervisor: ko.observable()
        };
        self.loaded = false;
        self.gradeFilters = {
            grade: ko.observable(),
            order: ko.observable(),
            course_id: ko.observable()
        };
        self.newGrade = {
            studentIndex: ko.observable(),
            courseID: ko.observable(),
            createdAt: ko.observable(),
            grade: ko.observable(),
            student: ko.observable()
        };
        self.removeStudent = function(student) {
            self.students.remove(student)
        };
        self.setGrades = function(student) {
            self.grades.removeAll();
            self.newGrade.student(student);
            self.newGrade.studentIndex(student.index());
            loadGrades(self, student);
            self.loaded = true;

            // https://knockoutjs.com/documentation/click-binding.html#note-3-allowing-the-default-click-action
            return true;
        };
        self.removeGrade = function(grade) {
            $.ajax({
                url: resourceUrl(grade),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('Object removed from eStudent service');
                self.grades.remove(grade);
            });
        };
        self.removeCourse = function(course) {
            self.courses.remove(course);
            self.grades.removeAll();
        };
        self.saveNewStudent = function() {
            $.ajax({
                url: SERVER_URL + '/students',
                type: 'POST',
                dataType : "json",
                contentType: "application/json",
                data: ko.mapping.toJSON(self.newStudent)
            }).done(function(data) {
                self.students.push(new ObservableObject(data));
                self.newStudent.firstName('');
                self.newStudent.lastName('');
                self.newStudent.dateOfBirth('');
            });
        };
        self.saveNewCourse = function() {
            $.ajax({
                url: SERVER_URL + '/courses',
                type: 'POST',
                dataType : "json",
                contentType: "application/json",
                data: ko.mapping.toJSON(self.newCourse)
            }).done(function(data) {
                self.courses.push(new ObservableObject(data));
                self.newCourse.name('');
                self.newCourse.supervisor('');
            });
        };
        self.saveNewGrade = function() {
            $.ajax({
                url: resourceUrl(self.newGrade.student(), 'grades'),
                type: 'POST',
                dataType : "json",
                contentType: "application/json",
                data: ko.mapping.toJSON(self.newGrade)
            }).done(function(data) {
                self.grades.push(new ObservableObject(data));
                self.newGrade.createdAt('');
            });
        };

        Object.keys(self.studentFilters).forEach(function (key) {
            self.studentFilters[key].subscribe(function (val) {
                // Disable auto delete from database
                if (self.studentSubscription) {
                    self.studentSubscription.dispose();
                }

                // Clear list of students
                self.students.removeAll();

                // Load new data
                loadStudents(self);
            });
        });

        Object.keys(self.courseFilters).forEach(function (key) {
            self.courseFilters[key].subscribe(function (val) {
                // Disable auto delete from database
                if (self.courseSubscription) {
                    self.courseSubscription.dispose();
                }

                // Clear list of courses
                self.courses.removeAll();

                // Load new courses
                loadCourses(self);
            });
        });

        Object.keys(self.gradeFilters).forEach(function (key) {
            self.gradeFilters[key].subscribe(function (val) {
                if (self.loaded && self.newGrade.student()) {
                    // Clear list of grades
                    self.grades.removeAll();

                    // Load new grades
                    loadGrades(self, self.newGrade.student());
                }
            });
        });
    }
    let model = new StateViewModel();
    ko.applyBindings(model);

    loadCourses(model);
});

function ObservableObject(data) {
    let self = this;
    ko.mapping.fromJS(data, {}, self);

    ko.computed(function() {
        return ko.mapping.toJSON(self);
    }).subscribe(function(res) {
        let resource = ko.mapping.fromJSON(res);
        $.ajax({
            url: resourceUrl(resource),
            type: 'PUT',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self)
        }).done(function(data) {
            console.log('Record updated');
        });
    });
}

function removedObjectCallback(changes) {
    changes.forEach(function(change) {
        // Student / Course deleted from database
        if (change.status === 'deleted') {
            $.ajax({
                url: resourceUrl(change.value),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('Object removed from service');
            });
        }
    })
}

function resourceUrl(record, type = 'self') {
    const links = record.link();
    const resourceUrl = links.find(function(link) {
        return link.params.rel() === type
    });

    return resourceUrl.href();
}

function loadStudents(model) {
    let jsonData = ko.toJS(model.studentFilters);
    if (jsonData.birth_date === "") {
        delete jsonData.birth_date;
    }
    $.ajax({
        url: SERVER_URL + '/students',
        type: 'GET',
        dataType : "json",
        data: jsonData,
        contentType: "application/json"
    }).done(function(result) {
        result.forEach(function (record) {
            model.students.push(new ObservableObject(record));
        });
        model.studentSubscription = model.students.subscribe(removedObjectCallback, null, 'arrayChange');
    });
}

function loadCourses(model) {
    let jsonData = ko.toJS(model.courseFilters);

    $.ajax({
        url: SERVER_URL + '/courses',
        type: 'GET',
        dataType : "json",
        data: jsonData,
        contentType: "application/json"
    }).done(function(result) {
        result.forEach(function (record) {
            model.courses.push(new ObservableObject(record));
        });
        model.courseSubscription = model.courses.subscribe(removedObjectCallback, null, 'arrayChange');
    });
}

function loadGrades(model, student) {
    let jsonData = ko.toJS(model.gradeFilters);

    $.ajax({
        url: resourceUrl(student, 'grades'),
        type: 'GET',
        dataType : "json",
        data: jsonData,
        contentType: "application/json"
    }).done(function(result) {
        result.forEach(function (record) {
            model.grades.push(new ObservableObject(record));
        });
    });
}