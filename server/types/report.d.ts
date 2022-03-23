interface Report {
   reportId: number
   reporter: number
   reported: number
   reporterProfile?: UserProfile
   reportedUserInfo: UserInfo
   reportedProfile?: UserProfile
   time: number
   tag: string
   additionalInfo: string
   resolved: string
}
