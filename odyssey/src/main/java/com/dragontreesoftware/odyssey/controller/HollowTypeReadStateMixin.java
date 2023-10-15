package com.dragontreesoftware.odyssey.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import org.springframework.boot.jackson.JsonMixin;

@JsonMixin(HollowTypeReadState.class)
@JsonIgnoreType
public class HollowTypeReadStateMixin {
}