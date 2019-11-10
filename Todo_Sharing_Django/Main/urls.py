from django.urls import path
from Main import views


urlpatterns = [
    path('', views.IndexView.as_view(), name='index_view'),
    path('list/', views.TodoListView.as_view(), name='todo_list_view'),
    path('api/insert/', views.insertRequest, name='api_insert'),
    path('api/update/', views.updateRequest, name='api_update'),
    path('api/delete/', views.deleteRequest, name='api_delete'),
    path('json/datas/', views.get_todo_db, name='json_todo_datas'),
]