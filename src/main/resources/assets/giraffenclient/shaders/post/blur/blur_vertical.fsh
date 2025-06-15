#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = vec4(0.0);
    float weights[5] = float[](0.227027027, 0.316216216, 0.070270270, 0.026216216, 0.016216216);

    for (int i = -4; i <= 4; i++) {
        color += texture(DiffuseSampler, texCoord + vec2(0.0, float(i) / InSize.y)) * weights[abs(i)];
    }

    fragColor = color;
}