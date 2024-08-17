package io.hexlet.typoreporter.service.dto.oauth2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PrivateEmailList {
    private List<PrivateEmail> privateEmailList;
}
