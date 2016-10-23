package math;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by lowery on 10/22/2016.
 */
public class VectorMath {

    public static float[] identity() {
        float[] i = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        };

        return i;
    }

    public static float[] translate(float x, float y, float z) {
        float[] t = {
            1.0f, 0.0f, 0.0f, x,
            0.0f, 1.0f, 0.0f, y,
            0.0f, 0.0f, 1.0f, z,
            0.0f, 0.0f, 0.0f, 1
        };

        return t;
    }

    public static float[] rotate(float x, float y, float z) {
        float[] r = {
            (float)(cos(y)*cos(z)),
            (float)(cos(x)*sin(z) + sin(x)*sin(y)*cos(z)),
            (float)(sin(x)*sin(z) - cos(x)*sin(y)*cos(z)),
            0.0f,

            (float)(-cos(y)*sin(z)),
            (float)(cos(x)*cos(z) - sin(x)*sin(y)*sin(z)),
            (float)(sin(x)*cos(z) + cos(x)*sin(y)*sin(z)),
            0.0f,

            (float)(sin(y)),
            (float)(-sin(x)*cos(y)),
            (float)(cos(x)*cos(y)),
            0.0f,

            0.0f,
            0.0f,
            0.0f,
            1.0f
        };

        return r;
    }

    public static float[] scale(float x, float y, float z) {
        float[] s = {
            x, 0.0f, 0.0f, 0.0f,
            0.0f, y, 0.0f, 0.0f,
            0.0f, 0.0f, z, 0.0f,
            0.0f, 0.0f, 0.0f, 1
        };

        return s;
    }

    public static float[] multiply(float[] a, float[] b) {
        float[] c = new float[16];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0.0f;

                for (int k = 0; k < 4; k++) {
                    sum += a[(i*4)+k] * b[(k*4)+j];
                }

                c[(i*4)+j] = sum;
            }
        }

        return c;
    }
}
