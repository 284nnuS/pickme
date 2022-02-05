/** @type {import("next").NextConfig} */
module.exports = {
   publicRuntimeConfig: {
      publicURL: process.env.NEXT_PUBLIC_URL,
   },
   trailingSlash: true,
}
