/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput;

import java.io.Serializable;

/**
 *222648767
 * @author smitm
 */

public class Student implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String studentName;
    private String studentId;
    private double studentScore;

    public Student(String studentName, String studentId, double studentScore)
    {
        this.studentName = studentName;
        this.studentId = studentId;
        this.studentScore = studentScore;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getStudentId()
    {
        return studentId;
    }

    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public double getStudentScore()
    {
        return studentScore;
    }

    public void setStudentScore(double studentScore)
    {
        this.studentScore = studentScore;
    }

    @Override
    public String toString()
    {
        return "Name: " + studentName
                + ", ID: " + studentId
                + ", Score: " + studentScore;
    }
}
