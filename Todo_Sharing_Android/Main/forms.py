from django import forms
from Main.models import *


class TodoForm(forms.Form):
    # TODO追加用コンテンツ
    content = forms.CharField(max_length=255,
                              required=False,
                              widget=forms.TextInput(attrs={'class':'form-control'}))
    # TODO編集用コンテンツ
    modal_content = forms.CharField(max_length=255,
                                    required=False,
                                    widget=forms.TextInput(attrs={'id':'todo-content',
                                                                  'class':'form-control',
                                                                  'onInput':'alertValue(this)'}))
    # TODO編集用コンテンツ(チェックボックス)
    modal_checkbox = forms.BooleanField(required=False,
                                        widget=forms.CheckboxInput(attrs={'class':'form-check-input custom-control-input',
                                                                          'id':'complete-check',
                                                                          'onChange':'alertValue(this)'}))
