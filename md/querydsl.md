## 환경 변수 설정 👀
깃허브를 사용하다 보면 프로젝트의 yaml에서 설정한, 올리고 싶지 않은 개인 정보들을 올릴 수 있다. <br>
이제부터는 환경 변수 설정을 통하여 이를 방지하도록 하자!!
<hr>

- Environment 설정 방법
  1. mysql.yaml의 `password`가 그대로 노출 되는 것을 볼 수 있다
  ![이미지](/img/num3.png)
  2. Configuration의 Edit.. 클릭
  3. Modify options의 Enviroment variables 클릭
  4. 환경 변수 추가
  ![이미지](/img/num4.png)
  5. 추가한 환경 변수를 바탕으로 mysql.yaml의 password수정
  ![이미지](/img/num5.png)
