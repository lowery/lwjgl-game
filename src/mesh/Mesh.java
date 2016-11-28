package mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 11/7/2016.
 */
public class Mesh {
    private List<Vertex> vertices = new ArrayList<>();
    private BoundingBox boundingBox;
    private float scale;

    public void addVertex(Vertex v) {
        vertices.add(v);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public float[] getVertexData() {
        float[] vertexData = new float[vertices.size() * 6];

        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);

            float[] position = v.getPosition();
            float[] texCoords = v.getTexCoords();

            vertexData[i*6] = position[0]/getScale();
            vertexData[(i*6)+1] = position[1]/getScale();
            vertexData[(i*6)+2] = position[2]/getScale();
            vertexData[(i*6)+3] = 1.0f;

            vertexData[(i*6)+4] = texCoords[0];
            vertexData[(i*6)+5] = texCoords[1];
        }

        return vertexData;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
}
