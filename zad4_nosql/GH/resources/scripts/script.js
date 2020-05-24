'use strict';
const SERVER_URL = 'http://localhost:8000';

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
    });
}

function loadGrades(model, student) {
    let jsonData = ko.toJS(model.gradeFilters);
    // let a = resourceUrl(student, 'grades');
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

function resourceUrl(record, type = 'self') {
    const links = record.link();
    const resourceUrl = links.find(function(link) {
        return link.params.rel() === type
    });

    return SERVER_URL + resourceUrl.href();
}

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
            console.log('state updated');
        });
    });
}

function ObservableGrade(data) {
    let self = this;
    ko.mapping.fromJS(data, {}, self);

    ko.computed(function() {
        return ko.mapping.toJSON(self);
    });
}


$(document).ready(function(){

    let StateViewModel = function () {
        let self = this;
        self.students = ko.observableArray();
        self.courses = ko.observableArray();
        self.grades = ko.observableArray();
        self.possibleGradeValue = ko.observableArray([
            {name: '2.0', value: '2.0'},
            {name: '3.0', value: '3.0'},
            {name: '3.5', value: '3.5'},
            {name: '4.0', value: '4.0'},
            {name: '4.5', value: '4.5'},
            {name: '5.0', value: '5.0'}
        ]);
        self.searchOptions = ko.observableArray([
            {name: 'Less', value: '-1'},
            {name: 'Equal', value: ''},
            {name: 'Greater', value: '1'}
        ]);
        self.newStudent = {
            firstName: ko.observable(),
            lastName: ko.observable(),
            birthday: ko.observable()
        };
        self.studentSubscription = null;
        self.courseSubscription = null;
        self.gradeSubscription = null;
        self.newCourse = {
            name: ko.observable(),
            lecturer: ko.observable()
        };
        self.studentFilters = {
            firstName: ko.observable(),
            lastName: ko.observable(),
            birthday: ko.observable(),
            birthdayCompare: ko.observable()
        };
        self.courseFilters = {
            name: ko.observable(),
            lecturer: ko.observable()
        };
        self.loaded = false;
        self.gradeFilters = {
            value: ko.observable(),
            valueCompare: ko.observable(),
            course: ko.observable(),
            date: ko.observable(),
            dateCompare: ko.observable()
        };
        self.newGrade = {
            studentId: ko.observable(),
            course: ko.observable(),
            date: ko.observable(),
            value: ko.observable(),
            student: ko.observable(),
            studentFirstName: ko.observable(),
            studentLastName: ko.observable()
        };
        self.removeStudent = function(student) {
            $.ajax({
                url: resourceUrl(student),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('deleted');
                self.students.remove(student);
            });
        };
        self.setGrades = function(student) {
            // location.href = "#grades";
            self.grades.removeAll();
            self.newGrade.student(student);
            self.newGrade.studentId(student.index());
            self.newGrade.studentFirstName(student.firstName());
            self.newGrade.studentLastName(student.lastName());

            loadGrades(self, student);
            self.loaded = true;

            return true;
        };
        self.removeGrade = function(grade) {
            $.ajax({
                url: resourceUrl(grade),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('deleted');
                self.grades.remove(grade);
            });
        };
        self.removeCourse = function(course) {
            $.ajax({
                url: resourceUrl(course),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('deleted');
                self.courses.remove(course);
                self.grades.removeAll();
            });
        };
        self.saveNewStudent = function() {
            console.log(self.newStudent);
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
                self.newStudent.birthday('');
            }).fail(function(xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
            })
            ;
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
                self.newCourse.lecturer('');
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
                self.newGrade.date('');
                self.newGrade.value('');
                self.newGrade.course('');
            })
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
                if (self.gradeSubscription) {
                    self.gradeSubscription.dispose();
                }
                if (self.loaded && self.newGrade.student()) {
                    // Clear list of grades
                    self.grades.removeAll();

                    // Load new grades
                    loadGrades(self, self.newGrade.student());
                }
            });
        });
    };



    let model = new StateViewModel();
    ko.applyBindings(model);

    loadStudents(model);
    loadCourses(model);
});