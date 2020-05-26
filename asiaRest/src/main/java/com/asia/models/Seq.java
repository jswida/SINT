package com.asia.models;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
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