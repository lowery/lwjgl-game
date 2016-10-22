#version 450 core

layout(location = 0)in vec4 position;
layout(location = 1)in vec4 offset;

void main(void)
{
     // Index into our array using gl_VertexID
     gl_Position = position + offset;
}