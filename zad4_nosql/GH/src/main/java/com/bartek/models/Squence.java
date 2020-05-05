package com.bartek.models;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

public class Squence {
    @Id
    ObjectId _id;
    private Long studentIndex;
    private Long courseID;
    private Long gradeID;

    public Squence() {
    }

    public Squence(Long studentIndex, Long courseID, Long gradeID) {
        this.studentIndex = studentIndex;
        this.courseID = courseID;
        this.gradeID = gradeID;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Long getStudentIndex() {
        return studentIndex;
    }

    public void setStudentIndex(Long studentIndex) {
        this.studentIndex = studentIndex;
    }

    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(Long courseID) {
        this.courseID = courseID;
    }

    public Long getGradeID() {
        return gradeID;
    }

    public void setGradeID(Long gradeID) {
        this.gradeID = gradeID;
    }
}
