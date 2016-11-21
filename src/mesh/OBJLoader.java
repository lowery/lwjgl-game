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

    public static void main(String[] args) {
        OBJLoader loader = new OBJLoader();
        Mesh mesh = loader.load("wooden_crate.obj");
        List<Vertex> vertexData = mesh.getVertices();

        float[] vertexPositions = new float[vertexData.size() * 6];

        for (int i = 0; i < vertexData.size(); i++) {
            Vertex v = vertexData.get(i);

            float[] position = v.getPosition();
            float[] texCoords = v.getTexCoords();

            vertexPositions[i*6] = position[0]/mesh.getScale();
            vertexPositions[(i*6)+1] = position[1]/mesh.getScale();
            vertexPositions[(i*6)+2] = position[2]/mesh.getScale();
            vertexPositions[(i*6)+3] = 1.0f;

            vertexPositions[(i*6)+4] = texCoords[0];
            vertexPositions[(i*6)+5] = texCoords[1];
        }

        loader.printVertices();
/*
        for (int i = 0; i < vertexPositions.length; i++) {
            System.out.print(vertexPositions[i]);

            if ((i+1) % 6 == 0) {
                System.out.println();
            } else {
                System.out.print(", ");
            }
        }
*/
    }

    public void printVertices() {
        for (float[] vertex : vertices) {

            System.out.printf("%f %f %f\n", vertex[0]/(maxLength*2),vertex[1]/(maxLength*2),vertex[2]/(maxLength*2));
        }

        System.out.println("scale: {" + (maxLength*2) + ", " + (maxLength*2) + ", " + (maxLength*2) + "}");
    }

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

                        // Check max length for scale
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
