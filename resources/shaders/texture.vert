#version 450 core

layout (location = 0) in vec4 position;
//layout (location = 1) in vec4 color;
layout (location = 1) in vec2 texCoord;
layout(location = 2)uniform mat4 translate;


//out vec4 ourColor;
out vec2 TexCoord;

void main() {
    gl_Position = position * translate;
    //ourColor = color;
    TexCoord = texCoord;
}
