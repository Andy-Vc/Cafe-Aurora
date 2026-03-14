const fs = require('fs');
const content = `export const environment = {
  production: true,
  apiUrl: '${process.env.API_URL}',
  supabaseUrl: '${process.env.SUPABASE_URL}',
  supabaseAnonKey: '${process.env.SUPABASE_ANON_KEY}'
};`;
fs.writeFileSync('src/environments/environment.prod.ts', content);
console.log('environment.prod.ts generado ✅');