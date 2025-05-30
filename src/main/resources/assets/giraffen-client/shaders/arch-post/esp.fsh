#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;
uniform vec4 color;
uniform float outlineAlpha;
uniform float filledAlpha;
uniform float width;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 current = texture(DiffuseSampler, texCoord);
    vec4 prev = texture(PrevSampler, texCoord);

    float edge = smoothstep(0.0, 1.0, length(current.rgb - prev.rgb));

    vec4 outline = vec4(color.rgb, outlineAlpha * edge);
    vec4 fill = vec4(color.rgb, filledAlpha * (1.0 - edge));

    fragColor = fill + outline;
}
