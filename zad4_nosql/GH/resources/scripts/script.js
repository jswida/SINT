'use strict';
const URL = 'http://localhost:8000';

function loadStudents(model) {
    let jsonData = ko.toJS(model.searchStudent);
    $.ajax({
        url: URL + '/students',
        type: 'GET',
        dataType : "json",
        data: jsonData,
        contentType: "application/json"
    }).done(function(result) {
        result.forEach(function (record) {
            model.students.push(new ObservableObject(record));
        });
    }).fail(function(xhr, status, error) {
        console.log('Bad Request');
    });
}

function loadCourses(model) {
    let jsonData = ko.toJS(model.searchCourse);

    $.ajax({
        url: URL + '/courses',
        type: 'GET',
        dataType : "json",
        data: jsonData,
        contentType: "application/json"
    }).done(function(result) {
        result.forEach(function (record) {
            model.courses.push(new ObservableObject(record));
        });
    }).fail(function(xhr, status, error) {
        console.log('Bad Request');
    });
}

function loadGrades(model, student) {
    let jsonData = ko.toJS(model.searchGrade);
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
    }).fail(function(xhr, status, error) {
        console.log('Bad Request');
    });
}

function resourceUrl(record, type = 'self') {
    const links = record.link();
    const resourceUrl = links.find(function(link) {
        return link.params.rel() === type
    });
    return URL + resourceUrl.href();
}

// put
function ObservableObject(data) {
    let self = this;
    // konckout mapping
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
        }).fail(function(xhr, status, error) {
            console.log('Bad Request');
        });
    });
}

$(document).ready(function(){
    let StateViewModel = function () {
        let self = this;
        self.loaded = false;
        // arrays
        self.students = ko.observableArray();
        self.courses = ko.observableArray();
        self.grades = ko.observableArray();
        // student
        self.newStudent = {
            firstName: ko.observable(),
            lastName: ko.observable(),
            birthday: ko.observable()
        };
        self.searchStudent = {
            firstName: ko.observable(),
            lastName: ko.observable(),
            birthday: ko.observable(),
            birthdayCompare: ko.observable()
        };
        // course
        self.newCourse = {
            name: ko.observable(),
            lecturer: ko.observable()
        };
        self.searchCourse = {
            name: ko.observable(),
            lecturer: ko.observable()
        };
        // grade
        self.searchGrade = {
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
        self.gradeOptions = ko.observableArray([
            {name: '2.0', value: '2.0'},
            {name: '3.0', value: '3.0'},
            {name: '3.5', value: '3.5'},
            {name: '4.0', value: '4.0'},
            {name: '4.5', value: '4.5'},
            {name: '5.0', value: '5.0'}
        ]);
        // default search
        self.searchOptions = ko.observableArray([
            {name: 'Less', value: '-1'},
            {name: 'Equal', value: ''},
            {name: 'Greater', value: '1'}
        ]);
        
        // delete
        self.removeStudent = function(student) {
            $.ajax({
                url: resourceUrl(student),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('deleted');
                self.students.remove(student);
            }).fail(function(xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
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
            }).fail(function(xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
            });
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
            }).fail(function(xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
            });
        };

        // post
        self.postStudent = function() {
            $.ajax({
                url: URL + '/students',
                type: 'POST',
                dataType : "json",
                contentType: "application/json",
                data: ko.mapping.toJSON(self.newStudent)
            }).done(function(data) {
                self.students.push(new ObservableObject(data));
                self.newStudent.firstName('');
                self.newStudent.lastName('');
                self.newStudent.birthday('');
                console.log("posted");
            }).fail(function(xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
            });
        };
        
        self.postCourse = function() {
            $.ajax({
                url: URL + '/courses',
                type: 'POST',
                dataType : "json",
                contentType: "application/json",
                data: ko.mapping.toJSON(self.newCourse)
            }).done(function(data) {
                self.courses.push(new ObservableObject(data));
                self.newCourse.name('');
                self.newCourse.lecturer('');
            }).fail(function(xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
            });
        };
        
        self.postGrade = function() {
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
            }).fail(function(xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
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

        // filter
        Object.keys(self.searchStudent).forEach(function (key) {
            self.searchStudent[key].subscribe(function (val) {
                self.students.removeAll();
                loadStudents(self);
            });
        });

        Object.keys(self.searchCourse).forEach(function (key) {
            self.searchCourse[key].subscribe(function (val) {
                self.courses.removeAll();
                loadCourses(self);
            });
        });

        Object.keys(self.searchGrade).forEach(function (key) {
            self.searchGrade[key].subscribe(function (val) {
                if (self.loaded && self.newGrade.student()) {
                    self.grades.removeAll();
                    loadGrades(self, self.newGrade.student());
                }
            });
        });
    };

    // final load
    let model = new StateViewModel();
    ko.applyBindings(model);
    loadStudents(model);
    loadCourses(model);
});