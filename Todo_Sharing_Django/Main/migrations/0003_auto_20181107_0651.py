# Generated by Django 2.1.2 on 2018-11-06 21:51

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('Main', '0002_auto_20181106_2102'),
    ]

    operations = [
        migrations.RenameField(
            model_name='todo',
            old_name='check',
            new_name='check_flag',
        ),
    ]