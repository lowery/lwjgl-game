#version 450 core

layout(location = 0)in vec4 position;
layout(location = 1)uniform mat4 translate;

void main(void)
{
     // Index into our array using gl_VertexID
     gl_Position = position * translate;
}