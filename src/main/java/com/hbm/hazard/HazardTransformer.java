package com.hbm.hazard;

import java.util.List;

/** Source-ordered pre/post evaluation extension points, independent of clients. */
public interface HazardTransformer {
    default void transformPre(HazardStack stack, List<HazardEntry> entries) {}
    default void transformPost(HazardStack stack, List<HazardEntry> entries) {}
}
