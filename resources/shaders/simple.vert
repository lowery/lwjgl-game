#version 450 core

layout(location = 0)in vec4 position;
layout(location = 1)uniform mat4 translate;

layout(location = 2)uniform vec4 color;

out VS_OUT
{
    vec4 color;
} vs_out;

void main() {
	gl_Position = translate * position;
	//vs_out.color = position * 2.0 + vec4(0.5, 0.5, 0.5, 0.0);
	vs_out.color = color;
}
