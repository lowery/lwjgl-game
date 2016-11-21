package mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 11/7/2016.
 */
public class Mesh {
    private List<Vertex> vertices = new ArrayList<>();
    private Texture texture;
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
}
