package top.speedcubing.server.utils;

public enum WebhookURL {
    LOBBY("https://discord.com/api/webhooks/1226829359273345065/8NAdavQ9_c7c3VHuytzh3kYTAjd-zafutHXnaCvCyguzEpH_FZl3RYTv5Qf7LOus0gR8"),
    KNOCKBACKFFA("https://discord.com/api/webhooks/1226846446314065920/rhNCvAgii9vlezhenaRebxD3my3jm4pGxKeBa2eh3nE-fpAPg7FcZp7Qi23f7rKvU36u"),
    CLUTCH("https://discord.com/api/webhooks/1226846735691677798/NKx-EivJ0Hxb8NR1ReM-BMTjOhxV6XHySFNZGA3wch_ksd5iSzbQ-C-_hAuG0ky-5qxS"),
    MLGRUSH("https://discord.com/api/webhooks/1226846967590555648/5nMyrYY7eVpjPzXRrQ7ByYRClaMfwpzaTXtin5eBJRBPwzW_ipP9EpJmQtT2GaxhqlVY"),
    FASTBUILDER("https://discord.com/api/webhooks/1226847230510497922/_Ff7ylV7_bpAHA2aZuOOGS7aedkCWjIyCIRnj5eLtnlbqL-R_zAmhXnk2773GgWQTDu-"),
    PRACTICE("https://discord.com/api/webhooks/1226847484337455168/ivT0SClmAUPyOYRyIfXGY8z_vJ8Ku8tnrJ8sbZMb9dK5EkGQ_dGtpBs1h1p-54zLMpHP"),
    REDUCE("https://discord.com/api/webhooks/1226847677417918495/ythlyBDOCnlB1cj0ClQl9dt4Ai9KaylV-OcK1kGOs4V-01Y-eJCutRSPhs_1C793lkuK");

    private final String url;

    WebhookURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

