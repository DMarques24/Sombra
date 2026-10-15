// Apaga a conta de quem faz o pedido (obrigatório no Google Play para apps com contas).
// Só a chave service_role pode apagar utilizadores, por isso isto corre no servidor e nunca na app.
// O perfil e o histórico desaparecem com a conta ("on delete cascade" nas tabelas).
import { createClient } from "jsr:@supabase/supabase-js@2";

const json = (body: unknown, status = 200) =>
  new Response(JSON.stringify(body), { status, headers: { "Content-Type": "application/json" } });

Deno.serve(async (req) => {
  if (req.method !== "POST") return json({ error: "method_not_allowed" }, 405);

  const token = req.headers.get("Authorization")?.replace(/^Bearer\s+/i, "");
  if (!token) return json({ error: "missing_token" }, 401);

  const admin = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
    { auth: { persistSession: false, autoRefreshToken: false } },
  );

  // O utilizador vem do próprio token: ninguém consegue apagar a conta de outra pessoa
  const { data, error } = await admin.auth.getUser(token);
  if (error || !data.user) return json({ error: "invalid_token" }, 401);

  const { error: deleteError } = await admin.auth.admin.deleteUser(data.user.id);
  if (deleteError) return json({ error: "delete_failed" }, 500);

  return json({ deleted: true });
});
