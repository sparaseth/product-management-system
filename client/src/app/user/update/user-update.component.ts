import {Component, OnInit} from "@angular/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {User} from "../../model/user";
import {ActivatedRoute, Router} from "@angular/router";
import {Authority} from "../../model/authority";
import {CommonService} from "../../common/common.service";
import {api} from "../../constants/api";
import {NotificationService} from "../../notification/notification.service";

@Component({
    selector: 'user-update-component',
    templateUrl: 'user-update.component.html'
})
export class UserUpdateComponent implements OnInit {
    userForm: FormGroup;
    loading: boolean;
    user: User;
    availableAuthorities: Authority[];
    selectedAuthority: Authority;

    constructor(private notificationService: NotificationService,
                private userService: CommonService,
                private router: Router,
                private route: ActivatedRoute) {
        this.availableAuthorities = [];
        this.loading = false;
        this.user = new User();
        this.user.createDate = null;
    }

    ngOnInit(): void {
        this.createEmptyForm();
        this.fillForm();
    }

    private createEmptyForm(): void {
        this.userForm = new FormGroup({
            username: new FormControl('', Validators.required),
            password: new FormControl('', Validators.required)
        });
    }

    private fillForm(): void {
        this.userService.loadById(api.USER, this.route.snapshot.params['id'])
            .subscribe(
                (user: User) => {
                    this.user = user;
                    this.loadAuthorities();
                    this.userForm.setValue({
                        username: this.user.username,
                        password: this.user.password
                    });
                },
                error => this.logError(error));
    }

    private loadAuthorities(): void {
        this.userService.loadAll(api.AUTHORITY)
            .subscribe(
                availableAuthorities => {
                    this.availableAuthorities = availableAuthorities;
                    this.selectedAuthority = this.availableAuthorities.filter(it => this.user.authority.name == it.name)[0];
                },
                error => this.logError(error));
    }

    onSubmit(): void {
        this.loading = true;
        this.user.username = this.userForm.value.username;
        this.user.password = this.userForm.value.password;
        this.user.authority = this.selectedAuthority;
        this.userService.update(api.USER, this.user)
            .subscribe(
                () => {
                    this.notificationService.success("Profile info successfully updated");
                    this.router.navigate(['/']).then(() => this.router.navigate(['user']))
                },
                error => this.logError(error));
    }

    onDelete(): void {
        if (this.user.id !== "") {
            this.userService.remove(api.USER, this.user.id)
                .subscribe(
                    () => {
                        this.notificationService.success("User profile deleted");
                        this.router.navigate(['/']).then(() => this.router.navigate(['user']))
                    },
                    error => this.logError(error));
        }
    }

    logError(error: Error): void {
        this.loading = false;
        this.notificationService.error(error.message);
    }
}