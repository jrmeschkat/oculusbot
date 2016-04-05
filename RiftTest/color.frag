#version 330
layout (location = 0) out vec3 out_color;
in vec3 tri_color;
void main() {
	out_color = tri_color;
}
