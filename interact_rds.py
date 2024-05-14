import boto3
import psycopg2
def get_rds_credentials():
    client = boto3.client('secretsmanager', region_name='us-east-1')
    secret_name = 'my_rds_secret'
    response = client.get_secret_value(SecretId=secret_name)
    return json.loads(response['SecretString'])

def push_data_to_rds():
    creds = get_rds_credentials()
    conn = psycopg2.connect(
        host=creds['host'],
        database=creds['dbname'],
        user=creds['username'],
        password=creds['password']
    )
    cursor = conn.cursor()
    cursor.execute("INSERT INTO my_table (data) VALUES ('Sample Data');")
    conn.commit()
    cursor.close()

if __name__ == "__main__":
    push_data_to_rds()
