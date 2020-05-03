package com.asia.models;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

public class Seq {

    @Id
    ObjectId _id;
    private Long studentIndex;
    private Long courseID;
    private Long gradeID;

    public Seq() {
    }

    public Seq(Long studentIndex, Long courseID, Long gradeID) {
        this.studentIndex = studentIndex;
        this.courseID = courseID;
        this.gradeID = gradeID;
    }

    public Long getCourseID() {
        return courseID;
    }

    public Long getGradeID() {
        return gradeID;
    }

    public Long getStudentIndex() {
        return studentIndex;
    }
}
