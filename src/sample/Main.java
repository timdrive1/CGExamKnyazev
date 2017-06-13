package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.vecmath.*;
import java.util.ArrayList;

public class Main extends Application {
    static Node cameraNode;
    static Vector3d point = new Vector3d(0, 1, 0);
    static Vector3d l = new Vector3d(0, 0.5, -1);
    private double angleY = 0;
    private long time = 1;
    private long frameTime = 1;
    public long duration = 50;

    private byte[] pixels;
    private double[] zbuffer;

    AnimationTimer timer;

    public static void main(String[] args) {
        l.normalize();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Князев Графический Экзамен");
        Group root = new Group();
        Canvas canvas = new Canvas(800, 800);
        GridPane gp = new GridPane();

        Button start = new Button();
        start.setText("Стоп/Старт");
        start.setOnAction(event -> StartOrStop());

        Button speedUp = new Button();
        speedUp.setText("Быстрее");
        speedUp.setOnAction(event -> SpeedUp());

        Button slowDown = new Button();
        slowDown.setText("Медленнее");
        slowDown.setOnAction(event -> SlowDown());

        root.getChildren().add(canvas);
        gp.add(start, 0,1);
        gp.add(speedUp, 0,2);
        gp.add(slowDown, 0,3);
        root.getChildren().add(gp);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        final PixelWriter pw = gc.getPixelWriter();

        ArrayList<Node> meshNodes = new ArrayList<>();


        pixels = new byte[800 * 800 * 3];
        zbuffer = new double[800 * 800];


        Node cameraNode = new Node();

        cameraNode.camera = new Camera(1, 100, 1, Math.PI / 2);
        cameraNode.matrix.rotZ(0.92);
        cameraNode.matrix.setColumn(3, cameraNode.matrix.m30, cameraNode.matrix.m31, 10, cameraNode.matrix.m33);
        cameraNode.computeTransform();

        Main.cameraNode = cameraNode;


        Reader readFile = new Reader();
        readFile.readFile();

        Vector3d[] positions = new Vector3d[readFile.positions.length / 3];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Vector3d(readFile.positions[i * 3], readFile.positions[i * 3 + 1], readFile.positions[i * 3 + 2]);
        }

        Vector3d[] normals = new Vector3d[readFile.normals.length / 3];
        for (int i = 0; i < normals.length; i++) {
            normals[i] = new Vector3d(readFile.normals[i * 3], readFile.normals[i * 3 + 1], readFile.normals[i * 3 + 2]);
        }

        int[] index = new int[readFile.indices.length];
        for (int i = 0; i < readFile.indices.length; i++) {
            index[i] = readFile.indices[i] & 0xffff;
        }

        Vector4d[] joints = new Vector4d[readFile.joints.length / 4];
        for (int i = 0; i < joints.length; i++) {
            joints[i] = new Vector4d(readFile.joints[i * 4], readFile.joints[i * 4 + 1], readFile.joints[i * 4 + 2], readFile.joints[i * 4 + 3]);
        }
        Vector4d[] weights = new Vector4d[readFile.weights.length / 4];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new Vector4d(readFile.weights[i * 4], readFile.weights[i * 4 + 1], readFile.weights[i * 4 + 2], readFile.weights[i * 4 + 3]);
        }
        Matrix4d[] ibm = new Matrix4d[readFile.ibm.length / 16];

        for (int i = 0; i < ibm.length; i++) {
            ibm[i] = new Matrix4d();
            ibm[i].m00 = (readFile.ibm[i * 16]);
            ibm[i].m10 = (readFile.ibm[i * 16 + 1]);
            ibm[i].m20 = (readFile.ibm[i * 16 + 2]);
            ibm[i].m30 = (readFile.ibm[i * 16 + 3]);
            ibm[i].m01 = (readFile.ibm[i * 16 + 4]);
            ibm[i].m11 = (readFile.ibm[i * 16 + 5]);
            ibm[i].m21 = (readFile.ibm[i * 16 + 6]);
            ibm[i].m31 = (readFile.ibm[i * 16 + 7]);
            ibm[i].m02 = (readFile.ibm[i * 16 + 8]);
            ibm[i].m12 = (readFile.ibm[i * 16 + 9]);
            ibm[i].m22 = (readFile.ibm[i * 16 + 10]);
            ibm[i].m32 = (readFile.ibm[i * 16 + 11]);
            ibm[i].m03 = (readFile.ibm[i * 16 + 12]);
            ibm[i].m13 = (readFile.ibm[i * 16 + 13]);
            ibm[i].m23 = (readFile.ibm[i * 16 + 14]);
            ibm[i].m33 = (readFile.ibm[i * 16 + 15]);
        }

        Node zero = new Node();
        zero.matrix.setTranslation(new Vector3d(0.0, -3.156060017772689e-7, -4.1803297996521));
        zero.matrix.setRotation(new Quat4d(-0.7047404050827026, 0.0, 0.0, -0.7094652056694031));


        Node first = new Node(zero);
        first.matrix.setTranslation(new Vector3d(0.0, 4.18717098236084, 0.0));
        first.matrix.setRotation(new Quat4d(-0.0020521103870123626, -9.94789530750495e-8, -0.00029137087403796613, -0.999997854232788));
        Quat4d keyFrameZero = new Quat4d();
        first.matrix.get(keyFrameZero);
        Quat4d keyFrameFirst = (Quat4d) keyFrameZero.clone();
        Quat4d tmp = new Quat4d(0.2933785021305084, -0.00008614854596089572, -0.0002783441450446844, -0.9559963345527649);
        keyFrameFirst.mul(tmp);
        Quat4d keyFrameSecond = keyFrameZero;
        keyFrameZero.inverse();
        keyFrameFirst.inverse();
        angleY += 90;


        final TriangleMesh triangleMesh = new TriangleMesh(pixels, zbuffer, gc, positions, index, normals, joints, weights, ibm, zero, first);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                for (int i = 0; i < pixels.length; i++) pixels[i] = (byte) 255;
                for (int i = 0; i < zbuffer.length; i++) zbuffer[i] = -1;

                double v = ((double) frameTime / ((double) duration / 2));

                Quat4d previousFrame = (time >= duration / 2) ? keyFrameFirst : keyFrameZero;
                Quat4d nextFrame = (time >= duration / 2) ? keyFrameSecond : keyFrameFirst;

                Quat4d kek = new Quat4d();
                kek.interpolate(previousFrame, nextFrame, v);
                triangleMesh.matrix.rotY(angleY);

                triangleMesh.nodes[1].matrix.set(kek);


                triangleMesh.nodes[0].matrix.setRotation(keyFrameFirst);

                triangleMesh.draw();
                pw.setPixels(0, 0, 800, 800, PixelFormat.getByteRgbInstance(), pixels, 0, 800 * 3);

                time++;
                frameTime++;
                if (time >= (duration)) time = 0;
                if (frameTime >= (duration / 2)) frameTime = 0;
            }
        };
        timer.start();
    }
    boolean b = false;
    public void StartOrStop(){
        b=!b;
        if (b) timer.stop();
        if (!b) timer.start();
    }

    public void SpeedUp(){
        if (duration>20) {
            time = 0;
            frameTime = 0;
            duration -= 10;
        }
    }

    public void SlowDown(){
        if (duration<300){
            time = 0;
            frameTime = 0;
            duration += 10;
        }
    }
}