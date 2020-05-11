package com.bartek.nosql;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

public class Seq {

    @Id
    ObjectId _id;
    private Long studentId;
    private Long courseId;
    private Long gradeId;

    public Seq() {
    }

    public Seq(Long studentId, Long courseId, Long gradeId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.gradeId = gradeId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public Long getStudentId() {
        return studentId;
    }
}