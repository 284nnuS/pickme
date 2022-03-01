/** @type {import("next").NextConfig} */
module.exports = {
   publicRuntimeConfig: {
      publicURL: process.env.NEXTAUTH_URL,
   },
   trailingSlash: true,
}
