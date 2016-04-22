import requests, json, smtplib, logging

# API Request
client_cert_file_path = "client-cert.pem"
client_key_file_path = "client-key.pem"
k8s_api_server_url = "http://192.168.19.249:8080"
context_get_nodes = "/api/v1/nodes"

# SMTP server details
smtp_server = "smtp.gmail.com"
port = 587

# Email Details
user = "your@gmail.com"
password = "yourpassword"
recipient = "your@gmail.com"
subject = "Email subject"

log_file = 'k8s_node_load_alert.log'
logging.basicConfig(level=logging.ERROR, filename=log_file)


def get_node_statues():
    alert_message = ""

    try:
        cert = (client_cert_file_path, client_key_file_path)
        url = k8s_api_server_url + context_get_nodes
        request = requests.get(url, cert=cert, verify=False)
        request_json = request.text
        parsed_json_obj = json.loads(request_json)
        node_list = parsed_json_obj["items"]

        for node in node_list:
            node_conditions = node["status"]["conditions"]
            node_name = node["metadata"]["name"]
            alert_message += "Node name : " + node_name + "\n"
            alert_message += "----------------------------------------\n"
            for condition in node_conditions:
                type = condition["type"]
                status = condition["status"]
                reason = condition["reason"]
                message = condition["message"]
                if (type == "Ready" and status == "False") or (type == "OutOfDisk" and status == "True"):
                    alert_message += "Type : " + type + "\n" + "Status : " + status + "\n" + "Message : " + message + \
                                     "\n" + "Reason : " + reason + "\n"
                    alert_message += "----------------------------------------\n"

    except Exception:
        logging.exception("Error while getting node status")

    send_email_alert(alert_message)


def send_email_alert(body):
    gmail_user = user
    gmail_pwd = password
    FROM = user
    TO = recipient if type(recipient) is list else [recipient]
    SUBJECT = subject
    TEXT = body

    # Prepare actual message
    message = """\From: %s\nTo: %s\nSubject: %s\n\n%s
    """ % (FROM, ", ".join(TO), SUBJECT, TEXT)
    try:
        server = smtplib.SMTP(smtp_server, port)
        server.ehlo()
        server.starttls()
        server.login(gmail_user, gmail_pwd)
        server.sendmail(FROM, TO, message)
        server.close()
        logging.info("successfully sent the mail")
    except Exception:
        logging.exception("Error while sending the email")


get_node_statues()
