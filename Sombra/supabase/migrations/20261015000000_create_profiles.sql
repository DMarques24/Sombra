-- Perfil de cada pessoa da Sombra. Uma linha por utilizador do Supabase Auth.
create table if not exists public.profiles (
    id                   uuid primary key references auth.users (id) on delete cascade,
    name                 text        not null default '',
    email                text,
    city                 text        not null default '',
    skin_type            text check (skin_type in ('I', 'II', 'III', 'IV', 'V', 'VI')),
    sun_places           text[]      not null default '{}',
    onboarding_completed boolean     not null default false,
    updated_at           timestamptz not null default now()
);

-- RLS: cada pessoa só vê e altera a sua própria linha
alter table public.profiles enable row level security;

create policy "Ver o próprio perfil" on public.profiles
    for select to authenticated
    using ((select auth.uid()) = id);

create policy "Criar o próprio perfil" on public.profiles
    for insert to authenticated
    with check ((select auth.uid()) = id);

create policy "Alterar o próprio perfil" on public.profiles
    for update to authenticated
    using ((select auth.uid()) = id)
    with check ((select auth.uid()) = id);

-- updated_at muda sozinho em cada alteração
create or replace function public.set_updated_at()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create trigger profiles_set_updated_at
    before update on public.profiles
    for each row execute function public.set_updated_at();

-- Ao criar conta, a linha do perfil nasce logo com o nome e a cidade escritos no registo
-- (a app manda-os nos metadados do signup). Funciona mesmo com a confirmação de email ligada.
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    insert into public.profiles (id, email, name, city)
    values (
        new.id,
        new.email,
        coalesce(new.raw_user_meta_data ->> 'name', ''),
        coalesce(new.raw_user_meta_data ->> 'city', '')
    );
    return new;
end;
$$;

create trigger on_auth_user_created
    after insert on auth.users
    for each row execute function public.handle_new_user();
