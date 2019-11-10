from django.shortcuts import render
from django.views import generic, View
from django.http import JsonResponse, HttpResponse
from Main.models import *
from django.views.decorators.csrf import csrf_exempt
from django.middleware.csrf import get_token
from Main.forms import *
import requests


# Create your views here.

class IndexView(generic.TemplateView):
    template_name = 'Main/index.html'



class TodoListView(generic.FormView):
    template_name = 'Main/todo_list.html'
    form_class = TodoForm
    success_url = '/main/list/'

    # GETメソッドの受け取り部分
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    # POSTメソッドの受け取り部分
    def post(self, request, *args, **kwargs):
        return super().post(request, *args, **kwargs)

    # バリデーションを通った時に呼ばれるメソッド
    def form_valid(self, form):
        if 'add-todo' in self.request.POST:
            # todo追加リクエスト
            add_todo = form.cleaned_data['content']     # 新規todoコンテンツを取得
            todo = Todo.objects.create(content=add_todo, check_flag=0)
            todo.save()
        elif 'edit-todo' in self.request.POST:
            # todo編集リクエスト
            modal_content = form.cleaned_data['modal_content']      # 更新todoコンテンツを取得
            modal_checkbox = form.cleaned_data['modal_checkbox']    # 更新todo完了済みチェック
            todo_id = form.data['todo_id']                          # 更新todoのIDを取得
            Todo.objects.filter(id=todo_id).update(content=modal_content, check_flag=modal_checkbox)
        elif 'delete-todo' in self.request.POST:
            # todo削除リクエスト
            todo_id = form.data['todo_id']
            Todo.objects.filter(id=todo_id).delete()
        return super().form_valid(form)

    # バリデーションに失敗した時に呼ばれるメソッド
    def form_invalid(self, form):
        return super().form_invalid(form)

    # HTMLに送るデータを返却するメソッド
    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['todo_list'] = Todo.objects.all()
        return context



# JSONファイルレスポンス用メソッド
def get_todo_db(request):
    if request.method == "POST":
        return
    token = get_token(request)
    json_array = []
    datas = Todo.objects.all()
    for d in datas:
        json_array.append({"id":d.id, "content":d.content, "check_flag":d.check_flag})
    return JsonResponse({"data":json_array})

@csrf_exempt
def insertRequest(request):
    if request.POST['content']:
        todo_content = request.POST['content']
        object = Todo.objects.create(content=todo_content)
        object.save()
        return HttpResponse("OK", content_type="text/plain")
    else:
        return HttpResponse("ERROR", content_type="text/plain")

@csrf_exempt
def updateRequest(request):
    if request.POST['id']:
        todo_object = Todo.objects.filter(id=request.POST['id'])
        if request.POST['content'] and request.POST['check_flag']:
            todo_object.update(content=request.POST['content'], check_flag=request.POST['check_flag'])
        return HttpResponse("OK", content_type="text/plain")
    else:
        return HttpResponse("ERROR", content_type="text/plain")


@csrf_exempt
def deleteRequest(request):
    if request.POST['id']:
        Todo.objects.filter(id=request.POST['id']).delete()
        return HttpResponse("OK", content_type="text/plain")
    else:
        return HttpResponse("ERROR", content_type="text/plain")

