import request from "@/utils/request";

class Text2ImageAPI {

    /**
     * 添加生成任务
     *
     * @param data {LoginData}
     * @returns
     */
    static propmt(data) {
        return request<any>({
            url: "/api/authed/1.0/t2i/propmt",
            method: "post",
            data: data
        });
    }

    /**
     * 查询环境能力（是否启用本地SD）
     */
    static capability() {
        return request<any>({
            url: "/api/authed/1.0/t2i/capability",
            method: "get"
        });
    }

    /**
     * 取消任务
     *
     * @param data {LoginData}
     * @returns
     */
    static canelGen(data) {
        return request<any>({
            url: "/api/authed/1.0/t2i/canel",
            method: "post",
            data: data
        });
    }

    /**
     * 优先任务
     *
     * @param data {LoginData}
     * @returns
     */
    static proprityTask(data) {
        return request<any>({
            url: "/api/authed/1.0/t2i/proprity",
            method: "post",
            data: data
        });
    }

    /**
     * 加载图片列表
     *
     * @param data {LoginData}
     * @returns
     */
    static listImages(data) {
        return request<any>({
            url: "/api/authed/1.0/t2i/list",
            method: "post",
            data: data
        });
    }
}

export default Text2ImageAPI;
