package org.slotterback.ui.controller;

import org.slotterback.StreamBuilderUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String getOperatorJson() {
        return StreamBuilderUtil.getOperatorJson();
    }

}
