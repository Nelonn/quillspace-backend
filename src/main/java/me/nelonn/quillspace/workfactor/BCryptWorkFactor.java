package me.nelonn.quillspace.workfactor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BCryptWorkFactor {
    private int strength;
    private long duration;
}