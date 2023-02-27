package ru.netology.dto;

import java.util.List;

public class GetListResponse {
    private List<GetListResponseItem> files;

    public List<GetListResponseItem> getFiles() {
        return files;
    }

    public void setFiles(List<GetListResponseItem> files) {
        this.files = files;
    }
}
