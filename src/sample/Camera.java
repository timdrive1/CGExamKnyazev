package sample;

import javax.vecmath.Matrix4d;

/**
 * Created by Тим on 31.05.2017.
 */
public class Camera {
    double near;
    double far;
    double aspectRatio;
    double fov;

    Matrix4d matrix = new Matrix4d();

    public Camera(double near, double far, double aspectRatio, double fov) {
        double t = near * Math.tan(fov / 2);
        double r = aspectRatio * t;

        matrix.m00 = (near / r);
        matrix.m11 = (near / t);
        matrix.m22 = ((far + near) / (near - far));
        matrix.m32 = (-1);
        matrix.m23 = (2 * far * near / (near - far));

        this.near = near;
        this.far = far;
        this.aspectRatio = aspectRatio;
        this.fov = fov;
    }
}