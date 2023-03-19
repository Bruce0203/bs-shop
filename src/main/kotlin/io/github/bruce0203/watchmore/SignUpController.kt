package io.github.bruce0203.watchmore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class SignUpController {

    @Autowired
    lateinit var memberService: MemberService
    @Autowired
    lateinit var memberRepository: MemberRepository

    @GetMapping("/signup")
    fun showSignup(model: Model): String {
        model.addAttribute("user", MemberEntity(username="", password=""))
        return "signup"
    }

    @PostMapping("/signup")
    fun signup(@ModelAttribute("user") user: MemberEntity, result: BindingResult, model: Model): String {
        val existingUser = memberRepository.findByUsername(user.username)
        return if (existingUser !== null && existingUser.username.isNotEmpty()) {
            result.rejectValue("username", "There is already an account registered with the same email")
            "signup"
        } else {
            memberService.joinUser(user)
            "home"
        }

    }

}