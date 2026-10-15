-- Histórico do protetor de cada pessoa (dá o XP, os níveis e os patches).
-- O id é gerado no telemóvel, para o mesmo evento nunca ficar duplicado ao sincronizar.
create table if not exists public.sunscreen_events (
    id       uuid primary key,
    user_id  uuid        not null default auth.uid() references auth.users (id) on delete cascade,
    type     text        not null check (type in ('Applied', 'ReappliedOnTime', 'ReappliedEarly', 'LeftSun')),
    at       timestamptz not null,
    spf      integer     not null,
    uv_index integer     not null,
    xp       integer     not null default 0
);

create index if not exists sunscreen_events_user_id_at_idx on public.sunscreen_events (user_id, at);

-- RLS: cada pessoa só vê e acrescenta os seus eventos (não há updates nem deletes pela app)
alter table public.sunscreen_events enable row level security;

create policy "Ver os próprios eventos" on public.sunscreen_events
    for select to authenticated
    using ((select auth.uid()) = user_id);

create policy "Criar os próprios eventos" on public.sunscreen_events
    for insert to authenticated
    with check ((select auth.uid()) = user_id);
