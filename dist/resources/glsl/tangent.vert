@version 4.20
@glsl define LIGHTNUM 100


in vec2 texcoord;
in vec3 normal;
in vec3 tangent;
in vec3 position;
            
out gl_PerVertex{
    vec4 gl_Position;
};

out vertexData{
	vec2 textureCoord;
	vec3 pos;
	vec3 norm;
	vec3 tan;
};

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 camera;

void main() {
    mat4 modelView = view * model;


    textureCoord = texcoord;
    norm = normalize(vec3(model * vec4(normal,0.0)));;
    tan = normalize(vec3(model * vec4(tangent,0.0)));
    pos = (model * vec4(position, 1.0f) ).xyz;

    vec4 P = view * vec4(pos, 1.0f);
    gl_Position = projection * P;
}