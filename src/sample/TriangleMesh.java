package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

/**
 * Created by Тим on 31.05.2017.
 */
public class TriangleMesh extends Mesh {
    int[] indices;

    Vector3d p = new Vector3d();

    Vector3d[] vertices;
    Vector3d[] verticesSkinned;

    Vector3d[] normals;
    Vector4d[] joints;
    Vector4d[] weights;
    Matrix4d[] ibm;
    Vector3d[] transformedPositions;
    Vector3d[] transformedNormals;
    Node[] nodes;


    Color color = Color.CYAN;

    double[] zbuffer;
    private Vector3d n = new Vector3d();
    private GraphicsContext gc;

    TriangleMesh(byte[] pixels, double[] zbuffer, GraphicsContext gc, Vector3d[] vertices, int[] indices, Vector3d[] normals, Vector4d[] joints, Vector4d[] weights, Matrix4d[] ibm, Node zero, Node first) {
        this.zbuffer = zbuffer;
        this.pixels = pixels;
        this.gc = gc;
        this.vertices = vertices;
        this.verticesSkinned = new Vector3d[vertices.length];
        this.normals = normals;
        this.indices = indices;
        this.joints = joints;
        this.weights = weights;
        this.ibm = ibm;
        nodes = new Node[]{zero, first};

        if (vertices != null) {
            init();
        }
    }

    protected void init() {
        transformedPositions = new Vector3d[vertices.length];
        transformedNormals = new Vector3d[normals.length];
        for (int i = 0; i < vertices.length; i++) {
            transformedPositions[i] = new Vector3d();
            transformedNormals[i] = new Vector3d();
        }
    }

    private Vector3d transformedPoint = new Vector3d();

    public Vector4d vecmatr4(Vector4d v, Matrix4d m){

        Vector4d c = new Vector4d();
        c.x = (v.x* m.m00 + v.y* m.m01 + v.z* m.m02 + v.w* m.m03);
        c.y = (v.x* m.m10 + v.y* m.m11 + v.z* m.m12 + v.w* m.m13);
        c.z = (v.x* m.m20 + v.y* m.m21 + v.z* m.m22 + v.w* m.m23);
        c.w = (v.x* m.m20 + v.y* m.m21 + v.z* m.m22 + v.w* m.m33);
        return c;
    }

    public Vector3d matrvec3(Matrix4d m, Vector3d v){

        Vector3d c = new Vector3d();
        c.x = (v.x* m.m00 + v.y* m.m01 + v.z* m.m02);
        c.y = (v.x* m.m10 + v.y* m.m11 + v.z* m.m12);
        c.z = (v.x* m.m20 + v.y* m.m21 + v.z* m.m22);
        return c;
    }


    public Vector4d matrvec4(Matrix4d m, Vector4d v){

        Vector4d c = new Vector4d();
        c.x = (v.x* m.m00 + v.y* m.m10 + v.z* m.m20 + v.w* m.m30);
        c.y = (v.x* m.m01 + v.y* m.m11 + v.z* m.m21 + v.w* m.m31);
        c.z = (v.x* m.m02 + v.y* m.m12 + v.z* m.m22 + v.w* m.m32);
        c.w = (v.x* m.m03 + v.y* m.m13 + v.z* m.m23 + v.w* m.m33);
        return c;
    }


    public double Hvecvec3(Vector3d m, Vector3d v){
        return m.x*v.x + m.y*v.y + m.z*v.z;
    }
    public Matrix4d Vvecvec4(Vector4d m, Vector4d v){
        Matrix4d c = new Matrix4d();

        c.m00 = m.x * v.x;
        c.m01 = m.x * v.y;
        c.m02 = m.x * v.z;
        c.m03 = m.x * v.w;

        c.m10 = m.y * v.x;
        c.m11 = m.y * v.y;
        c.m12 = m.y * v.z;
        c.m13 = m.y * v.w;


        c.m20 = m.z * v.x;
        c.m21 = m.z * v.y;
        c.m22 = m.z * v.z;
        c.m23 = m.z * v.w;

        c.m30 = m.w * v.x;
        c.m31 = m.w * v.y;
        c.m32 = m.w * v.z;
        c.m33 = m.w * v.w;

        return c;
    }




    public void draw() {
        for (int i = 0; i < vertices.length; i++) {
            transformNormals3d(normals[i], transformedNormals[i]);

            Node node = nodes[(int)joints[i].x];
            Vector4d vert = new Vector4d(vertices[i].x, vertices[i].y, vertices[i].z, 1.0);
            Matrix4d temp = node.matrix;
            Vector4d v = matrvec4(node.matrix, vert);

            verticesSkinned[i] = new Vector3d(v.x,v.y,v.z);
        }

        matrix.mul(Main.cameraNode.worldTransform, matrix);

        matrix.mul(Main.cameraNode.camera.matrix, matrix);

        for (int i = 0; i < vertices.length; i++) {
            Vector3d temp = verticesSkinned[i];

            transform3d(temp, transformedPositions[i]);
            transformedPositions[i].x = (transformedPositions[i].x + 1) * 400;
            transformedPositions[i].y = (transformedPositions[i].y + 1) * 400;

        }

        transform3d(Main.point, transformedPoint);
        transformedPoint.x = (transformedPoint.x + 1) * 400;
        transformedPoint.y = (transformedPoint.y + 1) * 400;

        for (int i = 0; i < indices.length / 3; i++) {
            final int i1 = indices[i * 3];
            final int i2 = indices[i * 3 + 1];
            final int i3 = indices[i * 3 + 2];

            final Vector3d p1 = transformedPositions[i1];
            final Vector3d p2 = transformedPositions[i2];
            final Vector3d p3 = transformedPositions[i3];


            final Vector3d n1 = transformedNormals[i1];
            final Vector3d n2 = transformedNormals[i2];
            final Vector3d n3 = transformedNormals[i3];

            drawTriangle(p1, p2, p3, n1, n2, n3);
        }
    }

    private void drawTriangle(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d n1, Vector3d n2, Vector3d n3) {
        int minX = (int) Math.ceil(Math.max(Math.min(Math.min(v1.x, v2.x), v3.x), 0));
        int maxX = (int) Math.ceil(Math.min(Math.max(Math.max(v1.x, v2.x), v3.x), 800));
        int minY = (int) Math.ceil(Math.max(Math.min(Math.min(v1.y, v2.y), v3.y), 0));
        int maxY = (int) Math.ceil(Math.min(Math.max(Math.max(v1.y, v2.y), v3.y), 800));
        double lyamdiv = (v2.y - v3.y) * (v1.x - v3.x) + (v3.x - v2.x) * (v1.y - v3.y);

        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                double lyam1 = ((v2.y - v3.y) * (i - v3.x) + (v3.x - v2.x) * (j - v3.y)) / lyamdiv;
                double lyam2 = ((v3.y - v1.y) * (i - v3.x) + (v1.x - v3.x) * (j - v3.y)) / lyamdiv;
                double lyam3 = 1 - lyam1 - lyam2;
                if ((lyam1 < 0) || (lyam2 < 0) || (lyam3 < 0)) continue;

                n.x = lyam1 * n1.x + lyam2 * n2.x + lyam3 * n3.x;
                n.y = lyam1 * n1.y + lyam2 * n2.y + lyam3 * n3.y;
                n.z = lyam1 * n1.z + lyam2 * n2.z + lyam3 * n3.z;
                n.normalize();


                p.x = lyam1 * v1.x + lyam2 * v2.x + lyam3 * v3.x;
                p.y = lyam1 * v1.y + lyam2 * v2.y + lyam3 * v3.y;
                p.z = lyam1 * v1.z + lyam2 * v2.z + lyam3 * v3.z;

                p.sub(transformedPoint);
                p.normalize();



                double light = Math.max(0.1, n.dot(Main.point));//+ Math.max(0.0, n.dot(p));


                double z = lyam1 * v1.z + lyam2 * v2.z + lyam3 * v3.z;

                if (zbuffer[800 * j + i] < z) {
                    pixels[(800 * j + i) * 3] = (byte) (color.getRed() * 255 * light);
                    pixels[(800 * j + i) * 3 + 1] = (byte) (color.getGreen() * 255 * light);
                    pixels[(800 * j + i) * 3 + 2] = (byte) (color.getBlue() * 255 * light);
                    zbuffer[800 * j + i] = z;
                }
            }
        }
    }
}