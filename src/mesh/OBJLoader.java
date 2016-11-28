package mesh;

import math.VectorMath;

import java.io.InputStream;
import java.util.*;

/**
 * Created by lowery on 11/7/2016.
 */
public class OBJLoader {
    private List<float[]> vertices = new ArrayList<>();
    private List<float[]> textureCoords = new ArrayList<>();
    private List<float[]> vertexNormals = new ArrayList<>();

    private float maxLength = 0;

    private float[] max = new float[3];
    private float[] min = new float[3];

    public Mesh load(String filename) {
        InputStream objFile = OBJLoader.class.getResourceAsStream("/models/" + filename);
        Scanner in = new Scanner(objFile);

        Mesh mesh = new Mesh();

        while (in.hasNext()) {
            String line = in.nextLine();

            if (line.startsWith("#")) {
                // skip this line, it's a comment
            } else {
                StringTokenizer tokenizer = new StringTokenizer(line);

                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();

                    if (token.equals("v")) {
                        float[] v = parseVector(tokenizer, 3);

                        // keep track of max and min for scale and bounding box
                        for (int i = 0; i < 3; i++) {
                            if (v[i] > max[i]) {
                                max[i] = v[i];
                            }

                            if (v[i] < min[i]) {
                                min[i] = v[i];
                            }

                            if (Math.abs(v[i]) > maxLength) {
                                maxLength = Math.abs(v[i]);
                            }
                        }

                        for (int i = 0; i < 3; i++) {
                            if (Math.abs(v[i]) > maxLength) {
                                maxLength = Math.abs(v[i]);
                            }
                        }

                        vertices.add(v);
                    } else if (token.equals("vt")) {
                        float[] vt = parseVector(tokenizer, 2);
                        textureCoords.add(vt);
                    } else if (token.equals("vn")) {
                        float[] vn = parseVector(tokenizer, 3);
                        vertexNormals.add(vn);
                    } else if (token.equals("f")) {
                        while (tokenizer.hasMoreTokens()) {
                            String[] indices = tokenizer.nextToken().split("/");

                            int vIndex = Integer.parseInt(indices[0]);
                            int vtIndex = Integer.parseInt(indices[1]);
                            int vnIndex = Integer.parseInt(indices[2]);

                            float[] position = vertices.get(vIndex-1);
                            float[] texCoord = textureCoords.get(vtIndex-1);
                            float[] normal = vertexNormals.get(vnIndex-1);

                            mesh.addVertex(new Vertex(position, texCoord, normal));
                        }
                    }
                }
            }
        }

        mesh.setScale(maxLength);
        mesh.setBoundingBox(new BoundingBox(max, min));

        return mesh;
    }

    private float[] parseVector(StringTokenizer tokenizer, int size) {
        float[] vector = new float[size];

        for (int i = 0; i < size; i++) {
            vector[i] = Float.parseFloat(tokenizer.nextToken());
        }

        return vector;
    }
}
