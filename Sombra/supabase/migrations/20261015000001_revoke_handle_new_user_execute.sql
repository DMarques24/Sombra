-- handle_new_user só corre pelo trigger do auth.users: ninguém a pode chamar pela API (/rest/v1/rpc)
revoke execute on function public.handle_new_user() from public, anon, authenticated;
