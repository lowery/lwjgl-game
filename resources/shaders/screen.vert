#version 450 core

layout(location = 0)in vec4 position;
layout(location = 1)uniform mat4 translate;

out VS_OUT
{
    vec4 color;
} vs_out;

void main(void)
{
     // Index into our array using gl_VertexID
     gl_Position = position * translate;
     vs_out.color = position * 2.0 + vec4(0.5, 0.5, 0.5, 0.0);
}