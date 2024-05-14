provider "aws" {
  region = "us-east-1"
}

resource "aws_rds_cluster" "my_rds" {
  cluster_identifier      = "my-rds-cluster"
  engine                  = "aurora-postgresql"
  master_username         = "admin"
  master_password         = "password123"
  database_name           = "mydb"
  
}

resource "aws_iam_role" "lambda_execution" {
  name = "lambda_execution_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_execution_policy" {
  role       = aws_iam_role.lambda_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_ecr_repository" "my_repo" {
  name = "my-repo"
}

output "ecr_repository_url" {
  value = aws_ecr_repository.my_repo.repository_url
}
