package mesh;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Created by lowery on 11/25/2016.
 */
public class BoundingBox {
    private final float[] max;
    private final float[] min;

    public BoundingBox(float[] max, float[] min) {
        this.max = max;
        this.min = min;
    }

    public float[] getPolyVertexData() {
        float[] vertexData = {
                // front face
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,

                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,

                // back face
                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], min[2], 1.0f, 0.0f, 0.0f,

                max[0], min[1], min[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], min[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,

                // left side
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], max[2], 1.0f, 0.0f, 0.0f,

                min[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], min[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,

                // right side
                max[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,

                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], min[2], 1.0f, 0.0f, 0.0f,

                // top
                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], max[2], 1.0f, 0.0f, 0.0f,

                max[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,

                // bottom

                min[0], min[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,

                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], min[2], 1.0f, 0.0f, 0.0f
        };

        return vertexData;
    }

    public float[] getVertexData() {
        /*float[] vertexData = {
                max[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,

                min[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f
        };*/

        float[] vertexData = {
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], max[2], 1.0f, 0.0f, 0.0f,

                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], min[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], min[2], 1.0f, 0.0f, 0.0f,

                min[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                min[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                min[0], min[1], min[2], 1.0f, 0.0f, 0.0f,

                max[0], max[1], min[2], 1.0f, 0.0f, 0.0f,
                max[0], max[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], max[2], 1.0f, 0.0f, 0.0f,
                max[0], min[1], min[2], 1.0f, 0.0f, 0.0f
        };

        return vertexData;
    }

    public Vector4f getMin() {
        return new Vector4f(min[0], min[1], min[2], 1.0f);
    }

    public Vector4f getMax() {
        return new Vector4f(max[0], max[1], max[2], 1.0f);
    }

    public boolean isPointInsideAABB(float x, float y, float z) {
        return (x >= min[0] && x <= max[0]) &&
                (y >= min[1] && y <= max[1]) &&
                (z >= min[2] && z <= max[2]);
    }
}
