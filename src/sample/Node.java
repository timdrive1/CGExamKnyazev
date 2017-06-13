package sample;

import javax.vecmath.Matrix4d;

/**
 * Created by Тим on 31.05.2017.
 */
class Node {
    Matrix4d matrix = new Matrix4d();
    Matrix4d worldTransform = new Matrix4d();

    Mesh mesh;
    Node parent;
    Camera camera;

    Node(Node parent) {
        matrix.setIdentity();
        this.parent = parent;
    }

    Node() {
        matrix.setIdentity();
    }

    private Matrix4d tempMat = new Matrix4d();

    void computeTransform() {
        Node tempNode = this;
        worldTransform.set(tempNode.matrix);
        while (true) {
            tempNode = tempNode.parent;
            if (tempNode == null)
                break;
            tempMat.set(tempNode.matrix);
            tempMat.mul(worldTransform);
            worldTransform.set(tempMat);
        }
    }
}
