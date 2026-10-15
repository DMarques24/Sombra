-- Defesa em profundidade: a RLS já bloqueia, mas os roles não precisam destes privilégios.
-- anon (sem sessão) nunca toca nas tabelas; TRUNCATE ignora a RLS e a app nunca o usa.
revoke all on table public.profiles, public.sunscreen_events from anon;
revoke truncate, references, trigger on table public.profiles, public.sunscreen_events from authenticated;
