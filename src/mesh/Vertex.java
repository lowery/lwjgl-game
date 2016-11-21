package mesh;

/**
 * Created by lowery on 11/7/2016.
 */
public class Vertex {
    private float[] position;
    private float[] texCoords;
    private float[] normal;

    public Vertex(float[] position, float[] texCoords, float[] normal) {
        this.position = position;
        this.texCoords = texCoords;
        this.normal = normal;
    }

    public float[] getPosition() {
        return position;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public float[] getNormal() {
        return normal;
    }
}
