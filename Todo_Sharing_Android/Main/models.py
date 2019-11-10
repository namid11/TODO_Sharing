from django.db import models

# Create your models here.

# Todoテーブル
class Todo(models.Model):
    """
    check : todoが完了したかのフラグ
    content : todoの内容
    """
    check_flag = models.BooleanField(default=0)
    content = models.CharField(max_length=256)