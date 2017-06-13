package sample;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Тим on 31.05.2017.
 */
public class Reader {
    short[] indices;
    float[] positions;
    float[] normals;
    short[] joints;
    float[] weights;
    float[] ibm;
    void readFile(){
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream("data/indices.bin");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            indices = new short[bytes.length / 2];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(indices);

            inputStream = new FileInputStream("data/positions.bin");
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            positions = new float[bytes.length / 4];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(positions);

            inputStream = new FileInputStream("data/normals.bin");
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            normals = new float[bytes.length / 4];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(normals);

            inputStream = new FileInputStream("data/joints.bin");
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            joints = new short[bytes.length / 2];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(joints);


            FileWriter os = new FileWriter("data/joints.txt");
            for (int i=0; i<joints.length/4;i++)
            {
                os.append(joints[i*4]+" "+joints[i*4+1]+" "+joints[i*4+2]+" "+joints[i*4+3]+"\n");
            }
            os.flush();
            os.close();


            inputStream = new FileInputStream("data/weights.bin");
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            weights = new float[bytes.length / 4];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(weights);

            inputStream = new FileInputStream("data/ibm.bin");
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            ibm = new float[bytes.length/4];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(ibm);

            System.out.print(true);
        }

        catch (IOException io) {

        }
    }
}