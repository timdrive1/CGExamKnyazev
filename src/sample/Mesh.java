package sample;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

/**
 * Created by Тим on 31.05.2017.
 */
public abstract class Mesh implements Drawable {
    byte[] pixels;
    Matrix4d matrix = new Matrix4d();
    Matrix4d normalMatrix = new Matrix4d();

    Mesh() {
        matrix.setIdentity();
    }

    void transform3d(Vector3d source, Vector3d target) {
        double w = source.x * matrix.getM30() + source.y * matrix.getM31() + source.z * matrix.getM32() + matrix.getM33();
        target.x = (source.x * matrix.getM00() + source.y * matrix.getM01() + source.z * matrix.getM02() + matrix.getM03()) / w;
        target.y = (source.x * matrix.getM10() + source.y * matrix.getM11() + source.z * matrix.getM12() + matrix.getM13()) / w;
        target.z = (source.x * matrix.getM20() + source.y * matrix.getM21() + source.z * matrix.getM22() + matrix.getM23()) / w;
    }

    void transformNormals3d(Vector3d source, Vector3d target) {
        normalMatrix.invert(matrix);
        normalMatrix.transpose();
        final Matrix4d matrix = normalMatrix;
        target.x = (source.x * matrix.getM00() + source.y * matrix.getM01() + source.z * matrix.getM02() + matrix.getM03());
        target.y = (source.x * matrix.getM10() + source.y * matrix.getM11() + source.z * matrix.getM12() + matrix.getM13());
        target.z = (source.x * matrix.getM20() + source.y * matrix.getM21() + source.z * matrix.getM22() + matrix.getM23());
    }

}
